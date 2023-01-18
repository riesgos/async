import { Wps, Database, MessageBus, KvPair, WpsOptionInput } from './infra';
import { Post } from './post';
import { listIntersection, listExcept, permutations, Queue, listFilter, Set } from './utils';
import hash from 'object-hash';
import objectHash from 'object-hash';




class InputMemory {
    private memory: {[processId: number]: Set<KvPair>} = {};

    constructor() {}

    set(processId: number, i: KvPair) {
        if (!this.memory[processId]) this.memory[processId] = new Set<KvPair>((a,b) => a.name === b.name && a.value === b.value);
        this.memory[processId].add(i);
    }

    getParameterValues(processId: number, name: string): KvPair[] {
        const cache = this.memory[processId];
        if (!cache) return [];
        const entries = cache.get((d: KvPair) => d.name === name);
        return entries;
    }
}

class ParaComboMemory {
    private memory: {[processId: number]: Set<string>} = {};

    constructor() {}

    set(processId: number, paras: KvPair[]): boolean {
        paras.sort((a, b) => a.name < b.name ? -1 : 1);
        const hash = objectHash(paras);
        if (!this.memory[processId]) this.memory[processId] = new Set<string>();
        return this.memory[processId].add(hash);
    }

}


export class Wrapper {

    protected parameterComboQueue = new Queue<{processId: number, processors: string[], relevantParameters: KvPair[], irrelevantParameters: KvPair[]}>(30);
    protected inputMemory = new InputMemory();
    protected parameterMemory = new ParaComboMemory();

    constructor(protected name: string, protected mb: MessageBus, protected wps: Wps) {

        // loop that handles all queued up tasks to be run by this processor.
        const loop = () => {
            const entry = this.parameterComboQueue.dequeue();
            // if nothing to do, try again in a little while
            if (!entry) {
                setTimeout(loop, 100);
            }
            else {
                const {processId, processors, relevantParameters, irrelevantParameters} = entry;
                this.wps.execute(relevantParameters).then((products) => {
                    const newPost: Post = {
                        processId: processId,
                        processors: [...processors, this.name],
                        data: [...irrelevantParameters, ...relevantParameters, ...products]
                        // data: [...products]
                    };
                    // console.log(`${this.name} used ${relevantParameters.map(p => p.name + '/' + p.value).join(', ')} to calculate ${products.map(p => p.name + '/' + p.value).join(', ')}`)
                    this.mb.write('posts', newPost);
                    loop();
                });
            }
        };
        
        // subscription to message-bus that puts relevant tasks on the queue
        this.mb.subscribe('posts', async (post: Post) => {

            // if post already handled, not relevant anymore.
            if (post.processors.includes(this.name)) return;
            
            const parameters = post.data;
            const relevantParameters = this.getRelevantParameters(parameters);
            const irrelevantParameters = this.getIrrelevantParameters(parameters);
            const parameterCombinations = this.validParameterCombinations(post.processId, relevantParameters);
            for (const parameterCombination of parameterCombinations) {
                const isNewCombination = this.parameterMemory.set(post.processId, parameterCombination)
                if (isNewCombination) {
                    this.parameterComboQueue.enqueue({ processId: post.processId, processors: post.processors, relevantParameters: parameterCombination, irrelevantParameters });
                }
            }
        });


        loop();
    }


    protected validParameterCombinations(processId: number, parameters: KvPair[]): KvPair[][] {
        const parameterNames = parameters.map(p => p.name);
        const outputNames = this.getOutputNames();
        const requiredInputNames = this.getRequiredInputNames();
        const optionalInputNames = this.getOptionalInputNames();
        const givenRequiredInputNames = listFilter(parameterNames, requiredInputNames);
        const givenOptionalInputNames = listFilter(parameterNames, optionalInputNames);
        const missingRequiredInputNames = listExcept(requiredInputNames, givenRequiredInputNames);
        const missingOptionalInputNames = listExcept(optionalInputNames, givenOptionalInputNames);
        const givenRequiredInputValues = givenRequiredInputNames.map(name => parameters.find(p => p.name === name)).filter(v => v !== undefined) as KvPair[];
        const givenOptionalInputValues = givenOptionalInputNames.map(name => parameters.find(p => p.name === name)).filter(v => v !== undefined) as KvPair[];

        // 1: if the post already contains outputs from this service, return []
        const oldOutputs = listIntersection(parameterNames, outputNames);
        if (oldOutputs.length > 0) return [];

        // 2: fill memory for next time
        givenRequiredInputValues.map(i => this.inputMemory.set(processId, i));

        // 3: try to fill any non-specified parameters
        const missingOptionalParameterValues = missingOptionalInputNames.map(name => this.getOptionalParameterValues(name));
        const missingRequiredParameterValues = missingRequiredInputNames.map(name => this.inputMemory.getParameterValues(processId, name));
        if (missingRequiredParameterValues.find(v => v.length === 0)) return [];
        const missingParameterValues = [... missingOptionalParameterValues, ... missingRequiredParameterValues];
        if (missingParameterValues.length === 0) return [[...givenRequiredInputValues, ...givenOptionalInputValues]];
        const missingParameterPermutations = permutations(missingParameterValues);
        const validConfigs: KvPair[][] = [];
        for (const missingParameterPermutation of missingParameterPermutations) {
            const configuration = [... givenRequiredInputValues, ...givenOptionalInputValues, ... missingParameterPermutation];
            validConfigs.push(configuration);
        }
        return validConfigs;

    }


    private getOptionalParameterValues(name: string): KvPair[] {
        const para = this.getPara(name) as WpsOptionInput;
        const options = para.options.map(o => ({ name: name, value: o }));
        return options;
    }

    private getRelevantParameters(paras: KvPair[]): KvPair[] {
        const relevantParaNames = this.getParaNames();
        const relevantParas = paras.filter(p => relevantParaNames.includes(p.name));
        return relevantParas;
    }

    private getIrrelevantParameters(paras: KvPair[]): KvPair[] {
        const relevantParaNames = this.getParaNames();
        const irrelevantParas = paras.filter(p => !relevantParaNames.includes(p.name));
        return irrelevantParas;
    }

    private getParaNames(): string[] {
        return this.wps.inputs.map(i => i.name);
    }

    private getPara(name: string) {
        return this.wps.inputs.find(i => i.name === name);
    }

    private getOutputNames(): string[] {
        return this.wps.outputNames;
    }

    private getOptionalInputNames(): string[] {
        //@ts-ignore
        return this.wps.inputs.filter(i => i.options).map(i => i.name);
    }

    private getRequiredInputNames(): string[] {
        //@ts-ignore
        return this.wps.inputs.filter(i => !(i.options)).map(i => i.name);
    }

}

