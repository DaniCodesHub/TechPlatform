import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../models/order';
import { Navbar } from '../../shared/navbar/navbar';

@Component({
  selector: 'app-orders',
  imports: [Navbar],
  templateUrl: './orders.html',
  styleUrl: './orders.css'
})
export class Orders implements OnInit {

  private readonly orderService = inject(OrderService);
  private readonly cdr = inject(ChangeDetectorRef);

  orders: Order[] = [];

  ngOnInit(): void {
    this.orderService.getOrders().subscribe({
      next: orders => {
        this.orders = orders;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al cargar pedidos:', error);
      }
    });
  }
}