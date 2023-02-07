FROM gfzriesgos/shakyground

WORKDIR /usr/share/git/shakemap_sampler
COPY . .
# RUN pip3 install -r requirements.txt
RUN pip3 install Cython
RUN pip3 install gstools

# FROM continuumio/miniconda3

# WORKDIR /usr/share/git/shakemap_sampler
# COPY . .
# RUN conda config --add channels conda-forge
# RUN conda config --remove channels defaults
# RUN conda config --set channel_priority strict
# RUN conda env create --name sampler --file environment.yml
# RUN conda activate sampler


