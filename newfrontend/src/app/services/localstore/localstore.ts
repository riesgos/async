import { Injectable } from '@angular/core';


@Injectable({
  providedIn: 'root'
})
export class LocalstoreService {


    private keys = ['email', 'password'];

    constructor() {}

    public save(key: string, data: string) {
        if (!this.keys.includes(key)) {
            console.error(`Store hasn't been primed for this key: ${key}`);
            return;
        }
        localStorage.setItem(key, data);
    }

    public get(key: string): string | null {
        if (!this.keys.includes(key)) {
            console.error(`Store hasn't been primed for this key: ${key}`);
            return null;
        }
        return localStorage.getItem(key);
    }

    public getAll(keys: string[]): { [key: string]: string | null } {
        const allData: { [key: string]: string | null } = {};
        const allKeys = keys.length > 0 ? keys : this.keys;
        for (const key of allKeys) {
            allData[key] = this.get(key);
        }
        return allData;
    }

    public saveAll(data: { [key: string]: string }) {
        for (const key in data) {
            this.save(key, data[key]);
        }
    }
}