import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ParserService {
  
  constructor() { }

  parseFile(file: File) {
    throw new Error('Method not implemented.');
  }

}
