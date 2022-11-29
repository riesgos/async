import { Injectable } from '@angular/core';
import { UserOrder } from './pulsar.service';

@Injectable({
  providedIn: 'root'
})
export class ParserService {
  
  constructor() { }

  parseFile(file: File): UserOrder[] {
    const buffer = await file.arrayBuffer()
    throw new Error('Method not implemented.');
  }

}
