import { Injectable } from '@angular/core';
import { UserOrder } from './pulsar.service';

@Injectable({
  providedIn: 'root'
})
export class ParserService {
  
  constructor() { }

  async parseFile(file: File): Promise<UserOrder[]> {
    const dataString = await this.readFile(file);
    const data = JSON.parse(dataString);
    return data;
  }

  async readFile(file: File): Promise<string> {
    return await file.text();

    // const response$ = new Promise((resolve, reject) => {
    //   const reader = new FileReader();
    //   reader.onload = (e) => {
    //     resolve(e.target?.result);
    //   }
    //   reader.readAsText(file, 'UTF-8');
    // })

    // const buffer = await file.arrayBuffer();
    // const string = Buffer.from(buffer).toString('UTF-8');
    // const data = JSON.parse(string);
    // return data;
  }

}
