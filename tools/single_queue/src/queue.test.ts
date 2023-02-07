import { MessageBus, Database } from './infra';
import { Post } from './post';
import { Modelprop, Shakyground, Assetmaster, Deus, Ab, OneTwo } from './services';
import { Wrapper } from './wrappers';

/**
 * @TODO: 
 * 
 * Make jest-tests work with async processes ...
 * 
 */


test('check that all possible parameter combinations are obtained', async () => {


    const messageBus = new MessageBus();

    const abWrapper = new Wrapper('ab', messageBus, new Ab());
    const oneTwoWrapper = new Wrapper('12', messageBus, new OneTwo());

    const userRequest: Post = {
        processId: 1,
        processors: ['user'],
        data: []
    };
    
    
    
    const outputs = await new Promise<any[]>(resolve => {

        const outputs: any[] = [];
        
        messageBus.subscribe("posts", async (post: Post) => {
            outputs.push(post);
            if (outputs.length === 4) resolve(outputs);
        });
        
        
        messageBus.write("posts", userRequest);
    });


    expect(outputs.length).toBe(4);
});


test('check that deus runs all possible para-combos', async () => {


    const messageBus = new MessageBus();
    const database = new Database();

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
    
    
    
    const deusOutputs = await new Promise<any[]>(resolve => {

        const deusOutputs: any[] = [];
        
        messageBus.subscribe("posts", async (post: Post) => {
            const deusOutput = post.data.find(p => p.name === 'eqDamage');
            if (deusOutput) {
                deusOutputs.push(deusOutput);
                if (deusOutputs.length === 4) resolve(deusOutputs);
            }
        });
        
        
        messageBus.write("posts", userRequest);
    });


    expect(deusOutputs.length).toBe(4);
});
