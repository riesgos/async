// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

/* const apiKey = 'VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV';
const channelId = 1;
`wss://demo.piesocket.com/v3/${channelId}?api_key=${apiKey}&notify_self` */

const topic: string = 'test'
const producerConsumer: 'producer' | 'consumer' = 'consumer';

export const environment = {
  production: false,
  pulsarWS: `wss://socketsbay.com/wss/v2/2/demo/`
  // pulsarWS: `ws://rz-vm154.gfz-potsdam.de:8080/ws/v2/${producerConsumer}/non-persistent/public/standalone/digital-earth/${topic}`
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
