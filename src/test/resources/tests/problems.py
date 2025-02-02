

sam = f"""hello"""


obi = f"""
kenobi
"""

print("o") # todo: Why required?

class SurfacePointsTable:
    c = None
    """
    A dataclass to represent a table of surface points in a geological model.
    """
    f""""
    Hey
    """

    def __init__(self):
        self.v1 = 12
        self.v2 = None
        self.topography: Optional[Topography] = None

a,b = [1,2]

grid = SurfacePointsTable()
grid.v1; grid.v2

# %%
grid.values, grid.values_r

if subset is None:
    center[0], normal[0] = _plane_fit(xyz_coords)
else:
    for idx, i in enumerate(subset):
        center[idx], normal[idx] = _plane_fit(xyz_coords[i])


l0, l1 = grid.get_grid_args('custom')
l0, l1

def _repr_html_(self):
    elements_html = '<br>'.join([e._repr_html_() for e in self.elements])
    html = f"""
    <table style="border-left:1.2px solid black;>
      <tr><th colspan="2"><b>StructuralGroup:</b></th></tr>
      <tr><td>Name:</td><td>{self.name}</td></tr>
      <tr><td>Structural Relation:</td><td>{self.structural_relation}</td></tr>
      <tr><td>Elements:</td><td>{elements_html}</td></tr>
    </table>
        """
    return html

property_matrix = xr.DataArray(
    data=values[0][:, l0:l1].reshape(-1, *res),
    dims=['Properties', *xyz],
)

prob = data[...]
prob = data[..., 0]
prob = data['pole_vector'][..., 0]

union : dict[str, tuple] = None

union2 : Union[Type1 | list[str]] = None
union3 : Union[dict[str, tuple] | list[str]] = None

def a(b : Union[Type1 |  Type2]):
    pass

def map_stack_to_surfaces(gempy_model: GeoModel, mapping_object: Union[dict[str, list[str]] | dict[str, tuple]],
                          set_series: bool = True, remove_unused_series=True) -> StructuralFrame:
    pass

if True:
    foo = foo.bar('[')


if True:
    pass
# comment
elif True:
    pass