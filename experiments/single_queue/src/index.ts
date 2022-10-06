import { MessageBus } from './infra';
import { Post } from './post';
import { Modelprop, Shakyground, Assetmaster, Deus, Ab, OneTwo } from './services';
import { Wrapper } from './wrappers';


const messageBus = new MessageBus();

const modelpropWrapper = new Wrapper('modelprop', messageBus, new Modelprop());
const shakygroundWrapper = new Wrapper('shakyground', messageBus, new Shakyground());
const assetmasterWrapper = new Wrapper('assetmaster', messageBus, new Assetmaster());
const deusWrapper = new Wrapper('deus', messageBus, new Deus());


const userRequest: Post = {
  processId: 42,
  processors: ['user'],
  data: [
    {name: 'gmpe', value: 'gmpe1'},
    {name: 'eqParas', value: 'magnitude8.5'}
  ]  
};


// @TODO: if the user requests contains options, split them into single-value-posts
messageBus.write("posts", userRequest);
let i = 0;
messageBus.subscribe("posts", async (post: Post) => {
  console.log(i, post);
  i++;
})



