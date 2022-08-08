import { Component, HostBinding, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DbService } from 'src/app/services/db.service';

import { LayersService, VectorLayer } from '@dlr-eoc/services-layers';
import { MapStateService } from '@dlr-eoc/services-map-state';
import { IMapControls } from '@dlr-eoc/map-ol';

import { OsmTileLayer, EocLitemap, BlueMarbleTile } from '@dlr-eoc/base-layers-raster';
import { Product, ProductType } from '../../../../../node-test-wss/fastAPI-Types/index';

@Component({
  selector: 'app-catalog-view',
  templateUrl: './catalog-view.component.html',
  styleUrls: ['./catalog-view.component.scss'],
  providers: [LayersService, MapStateService]
})
export class CatalogViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  productTypes$: Observable<ProductType[]>;
  products$: Observable<Product[]>;

  navGroups: { [name: string]: boolean } = {};

  controls: IMapControls;
  constructor(private dbSvc: DbService,
    public layerSvc: LayersService,
    public mapStateSvc: MapStateService) {
    this.controls = {
      scaleLine: true
    }

    this.productTypes$ = this.dbSvc.getProductsTypes();
    this.products$ = this.dbSvc.getProducts();
  }

  ngOnInit(): void {
    this.addBaselayers();
    this.mapStateSvc.setExtent([-73.361, -34.120, -70.636, -31.826]);
    this.layerSvc.addLayer(new VectorLayer({
      id: 'quakeledger',
      name: 'quakeledger',
      type: 'geojson',
      url: `http://localhost:4200/assets/data/quakeledger.json`
    }));
  }

  addBaselayers() {
    const layers = [
      new OsmTileLayer({
        visible: false
      }),
      new EocLitemap({
        visible: true
      }),
      new BlueMarbleTile({
        visible: false
      })
    ];

    layers.map(l => this.layerSvc.addLayer(l, 'Baselayers'));
  }

  onNavGroupExpand($event: boolean, navType: string) {
    Object.keys(this.navGroups).forEach(k => {
      if (this.navGroups[k]) {
        this.navGroups[k] = false;
      } else {
        this.navGroups[k] = true;
      }
    });

    console.log(this.navGroups);
  }

}
