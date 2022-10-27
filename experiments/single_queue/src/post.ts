import { KvPair } from './infra';


export interface Post {
    /**
     * Process-id: one request sent in, 
     * will be modified by an unknown number of processors,
     * but always retains it's process-id
     */
    processId: number,
    processors: string[],
    data: KvPair[]
}
