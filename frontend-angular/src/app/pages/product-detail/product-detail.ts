import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-product-detail',
  imports: [],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail implements OnInit {
  productId!: number;

  constructor(private route: ActivatedRoute) {};

  ngOnInit() {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    console.log('Viewing product:', this.productId);
  }

}
