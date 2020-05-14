import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'product',
        loadChildren: () => import('./product/product.module').then(m => m.SimpleShopProductModule)
      },
      {
        path: 'address',
        loadChildren: () => import('./address/address.module').then(m => m.SimpleShopAddressModule)
      },
      {
        path: 'shopping-order',
        loadChildren: () => import('./shopping-order/shopping-order.module').then(m => m.SimpleShopShoppingOrderModule)
      },
      {
        path: 'product-order',
        loadChildren: () => import('./product-order/product-order.module').then(m => m.SimpleShopProductOrderModule)
      },
      {
        path: 'shipment',
        loadChildren: () => import('./shipment/shipment.module').then(m => m.SimpleShopShipmentModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class SimpleShopEntityModule {}
