import { Component, OnInit } from '@angular/core';
import config from 'capacitor.config';

@Component({
  selector: 'app-bottom-tabs',
  templateUrl: './bottom-tabs.page.html',
  styleUrls: ['./bottom-tabs.page.scss'],
})
export class BottomTabsPage implements OnInit {

  config = config;

  constructor() { }

  ngOnInit() {
  }

}
