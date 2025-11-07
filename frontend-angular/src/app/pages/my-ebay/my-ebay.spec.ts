import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyEbay } from './my-ebay';

describe('MyEbay', () => {
  let component: MyEbay;
  let fixture: ComponentFixture<MyEbay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyEbay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyEbay);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
