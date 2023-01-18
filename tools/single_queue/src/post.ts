import { KvPair } from './infra';


export interface Post {
    processId: number,
    processors: string[],
    data: KvPair[]
}
