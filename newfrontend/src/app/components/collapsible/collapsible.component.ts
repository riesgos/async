import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-collapsible',
  templateUrl: './collapsible.component.html',
  styleUrls: ['./collapsible.component.css']
})
export class CollapsibleComponent implements OnInit {

  public expanded = false;

  constructor() { }

  ngOnInit(): void {
  }

}
