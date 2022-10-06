
export function sleep(timeMs: number) {
    return new Promise(resolve => setTimeout(resolve, timeMs));
}


export function listExcept<T>(list: T[], except: T[]): T[] {
    const out: T[] = [];
    for (const entry of list) {
        if ( !except.includes(entry) ) {
            out.push(entry);
        }
    }
    return out;
}


export function listFilter<T>(list: T[], includes: T[]): T[] {
    const out: T[] = [];
    for (const entry of list) {
        for (const i of includes) {
            if (entry === i) {
                out.push(entry);
                break;
            }
        }
    }
    return out;
}


export function listIntersection<T>(list1: T[], list2: T[]): T[] {
    const ints: T[] = [];
    for (const i1 of list1) {
        for (const i2 of list2) {
            if (i1 === i2) {
                ints.push(i1);
                break;
            }
        }
    }
    return ints;
}


export function permutations<T>(data: T[][]): T[][] {
    if (data.length === 0) return [];
    if (data.length === 1) return data[0].map(v => [v]);

    const perms: T[][] = [];
    const firstGroup = data[0];
    const subPerms = permutations(data.slice(1));
    for (const entry of firstGroup) {
        for (const subPerm of subPerms) {
            perms.push([entry, ...subPerm]);
        }
    }

    return perms;
}


export class Queue<T> {
    private data: (T | null)[];
    private head = 0;
    private tail = 0;

    constructor(capacity: number) {
        this.data = Array(capacity).fill(0).map(v => null);
    }

    public enqueue(val: T): boolean {
        const location = this.tail;
        if (!this.data[location]) {
            this.data[location] = val;
            this.tail = this.shiftUp(this.tail);
            return true;
        } else {
            return false;
        }
    }

    public dequeue() {
        const location = this.head;
        const data = this.data[location];
        this.data[location] = null;
        this.head = this.shiftUp(location);
        return data;
    }

    private shiftUp(n: number) {
        return (n + 1) % this.data.length;
    }
}



export type SetEqualityFunction<T> = (a: T, b: T) => boolean;
export type SetSelectionPredicate<T> = (a: T) => boolean;

export class Set<T> {
    private data: T[] = [];

    constructor(private equalityFunction: SetEqualityFunction<T> = (a, b) => a === b) {}

    public add(entry: T): boolean {
        for (const d of this.data) {
            if (this.equalityFunction(entry, d)) return false;
        }
        this.data.push(entry);
        return true;
    }

    public get(predicate: SetSelectionPredicate<T>): T[] {
        const out: T[] = [];
        for (const d of this.data) {
            if (predicate(d)) {
                out.push(d);
            };
        }
        return out;
    }
}
