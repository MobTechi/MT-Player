import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BottomTabsPage } from './bottom-tabs.page';

describe('BottomTabsPage', () => {
  let component: BottomTabsPage;
  let fixture: ComponentFixture<BottomTabsPage>;

  beforeEach(async(() => {
    fixture = TestBed.createComponent(BottomTabsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
