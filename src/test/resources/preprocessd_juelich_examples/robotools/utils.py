"""Utility functions that are relevant for worklist commands."""
import collections
import logging
import math
from typing import Dict, Iterable, List, Optional, Tuple, Union
import numpy
from robotools.evotools.types import Tip, int_to_tip
from robotools.worklists.exceptions import InvalidOperationError
from .. import liquidhandling
__all__ = ('prepare_aspirate_dispense_parameters', 'optimize_partition_by', 'partition_volume', 'partition_by_column')
logger = logging.getLogger(__name__)

def prepare_aspirate_dispense_parameters(rack_label, position, volume, liquid_class='', tip=Tip.Any, rack_id='', tube_id='', rack_type='', forced_rack_type='', max_volume=None):
    """Validates and prepares aspirate/dispense parameters.

    Parameters
    ----------
    rack_label : str
        User-defined labware name (max 32 characters)
    position : int
        Number of the well
    volume : float
        Volume in microliters (will be rounded to 2 decimal places)
    liquid_class : str, optional
        Overrides the liquid class for this step (max 32 characters)
    tip : Tip, int or Iterable of Tip / int, optional
        Tip that will be selected (Tip, 1-8 or Iterable of the former two)
    rack_id : str, optional
        Barcode of the labware (max 32 characters)
    tube_id : str, optional
        Barcode of the tube (max 32 characters)
    rack_type : str, optional
        Configuration name of the labware (max 32 characters).
        An error is raised if it missmatches with the underlying worktable.
    forced_rack_type : str, optional
        Overrides rack_type from worktable
    max_volume : int, optional
        Maximum allowed volume

    Returns
    -------
    rack_label : str
        User-defined labware name (max 32 characters)
    position : int
        Number of the well
    volume : str
        Volume in microliters (will be rounded to 2 decimal places)
    liquid_class : str
        Overrides the liquid class for this step (max 32 characters)
    tip : Tip, int or Iterable of Tip / int
        Tip that will be selected (Tip, 1-8 or Iterable of the former two)
    rack_id : str
        Barcode of the labware (max 32 characters)
    tube_id : str
        Barcode of the tube (max 32 characters)
    rack_type : str
        Configuration name of the labware (max 32 characters).
        An error is raised if it missmatches with the underlying worktable.
    forced_rack_type : str
        Overrides rack_type from worktable
    """
    if rack_label is None:
        raise ValueError('Missing required parameter: rack_label')
    if not isinstance(rack_label, str) or len(rack_label) > 32 or ';' in rack_label:
        raise ValueError(f'Invalid rack_label: {rack_label}')
    if position is None:
        raise ValueError('Missing required parameter: position')
    if not isinstance(position, int) or position < 0:
        raise ValueError(f'Invalid position: {position}')
    if volume is None:
        raise ValueError('Missing required parameter: volume')
    try:
        volume = float(volume)
    except:
        raise ValueError(f'Invalid volume: {volume}')
    if volume < 0 or volume > 7158278 or numpy.isnan(volume):
        raise ValueError(f'Invalid volume: {volume}')
    if max_volume is not None and volume > max_volume:
        raise InvalidOperationError(f'Volume of {volume} exceeds max_volume.')
    if not isinstance(liquid_class, str) or ';' in liquid_class:
        raise ValueError(f'Invalid liquid_class: {liquid_class}')
    if isinstance(tip, int) and (not isinstance(tip, Tip)):
        tip = int_to_tip(tip)
    if isinstance(tip, collections.abc.Iterable):
        tips = []
        for element in tip:
            if isinstance(element, int) and (not isinstance(element, Tip)):
                tips.append(int_to_tip(element))
            elif isinstance(element, Tip):
                if element == -1:
                    raise ValueError('When Iterables are used, no Tip.Any elements are allowed. Pass just one Tip.Any instead.')
                tips.append(element)
            else:
                raise ValueError(f'If tip is an Iterable, it may only contain int or Tip values, not {type(element)}.')
        tip = sum(set(tips))
    elif not isinstance(tip, Tip):
        raise ValueError(f'tip must be an int between 1 and 8, Tip or Iterable, but was {type(tip)}.')
    if not isinstance(rack_id, str) or len(rack_id) > 32 or ';' in rack_id:
        raise ValueError(f'Invalid rack_id: {rack_id}')
    if not isinstance(rack_type, str) or len(rack_type) > 32 or ';' in rack_type:
        raise ValueError(f'Invalid rack_type: {rack_type}')
    if not isinstance(forced_rack_type, str) or len(forced_rack_type) > 32 or ';' in forced_rack_type:
        raise ValueError(f'Invalid forced_rack_type: {forced_rack_type}')
    volume_str = f'{numpy.round(volume, decimals=2):.2f}'
    tip = '' if tip == -1 else tip
    return (rack_label, position, volume_str, liquid_class, tip, rack_id, tube_id, rack_type, forced_rack_type)

def optimize_partition_by(source, destination, partition_by, label=None):
    """Determines optimal partitioning settings.

    Parameters
    ----------
    source (Labware): source labware object
    destination (Labware): destination labware object
    partition_by : str
    user-provided partitioning settings
    label : str
    label of the operation (optional)

    Returns
    -------
    partition_by : str
        Either 'source' or 'destination'
    """
    if not partition_by in {'auto', 'source', 'destination'}:
        raise ValueError(f'Invalid partition_by argument: {partition_by}')
    if partition_by == 'auto':
        if source.is_trough and (not destination.is_trough):
            partition_by = 'destination'
        else:
            partition_by = 'source'
    elif partition_by == 'source' and source.is_trough and (not destination.is_trough):
        logger.warning(f'Partitioning by "source" ({source.name}), which is a Trough while destination ({destination.name}) is not a Trough. This is potentially inefficient. Consider using partition_by="destination". (label={label})')
    elif partition_by == 'destination' and destination.is_trough and (not source.is_trough):
        logger.warning(f'Partitioning by "destination" ({destination.name}), which is a Trough while source ({source.name}) is not a Trough. This is potentially inefficient. Consider using partition_by="source" (label={label})')
    return partition_by

def partition_volume(volume, *, max_volume):
    """Partitions a pipetting volume into zero or more integer-valued volumes that are <= max_volume.

    Parameters
    ----------
    volume : float
        A volume to partition
    max_volume : int
        Maximum volume of a pipetting step

    Returns
    -------
    volumes : list
        Partitioned volumes
    """
    if volume == 0:
        return []
    if volume < max_volume:
        return [volume]
    isteps = math.ceil(volume / max_volume)
    step_volume = math.ceil(volume / isteps)
    volumes = [step_volume] * (isteps - 1)
    volumes.append(volume - numpy.sum(volumes))
    return volumes

def partition_by_column(sources, destinations, volumes, partition_by):
    """Partitions sources/destinations/volumes by the source column and sorts within those columns.

    Parameters
    ----------
    sources : list
        The source well ids; same length as destinations and volumes
    destinations : list
        The destination well ids; same length as sources and volumes
    volumes : list
        The volumes; same length as sources and destinations
    partition_by : str
        Either 'source' or 'destination'

    Returns
    -------
    column_groups : list
        A list of (sources, destinations, volumes)
    """
    column_groups_dd = collections.defaultdict(lambda : ([], [], []))
    for (s, d, v) in zip(sources, destinations, volumes):
        if partition_by == 'source':
            group = s[1:]
        elif partition_by == 'destination':
            group = d[1:]
        else:
            raise ValueError(f'Invalid `partition_by` parameter "{partition_by}""')
        column_groups_dd[group][0].append(s)
        column_groups_dd[group][1].append(d)
        column_groups_dd[group][2].append(v)
    column_groups = [column_groups_dd[col] for col in sorted(column_groups_dd.keys())]
    for (c, (srcs, dsts, vols)) in enumerate(column_groups):
        if partition_by == 'source':
            order = numpy.argsort(srcs)
        elif partition_by == 'destination':
            order = numpy.argsort(dsts)
        else:
            raise ValueError(f'Invalid `partition_by` parameter "{partition_by}""')
        column_groups[c] = (list(numpy.array(srcs)[order]), list(numpy.array(dsts)[order]), list(numpy.array(vols)[order]))
    return column_groups