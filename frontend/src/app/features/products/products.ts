import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../models/product';
import { Navbar } from '../../shared/navbar/navbar';

@Component({
  selector: 'app-products',
  imports: [Navbar],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products implements OnInit {

  private readonly productService = inject(ProductService);
  private readonly cdr = inject(ChangeDetectorRef);

  products: Product[] = [];

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: products => {
        this.products = products;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al cargar productos:', error);
      }
    });
  }
}