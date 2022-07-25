from distutils.core import setup

setup(
    name='Catalog app',
    version='0.0.1',
    description='Catalog backend for the RIESGOS async architecture',
    author='Nils Brinckmann',
    author_email='nils@gfz-potsdam.de',
    url='https://github.com/riesgos/async',
    packages_dir = {"": "catalog_app"}
)
