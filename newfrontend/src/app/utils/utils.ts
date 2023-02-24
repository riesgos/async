export function allTrue(arr: any[]): boolean {
    for (const entry of arr) {
        if (!!entry) return false;
    }
    return true;
}