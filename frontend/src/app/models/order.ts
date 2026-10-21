export interface OrderItem {
  id: number;
  productId: number;
  quantity: number;
  unitPrice: number;
}

export interface Order {
  id: number;
  date: string;
  items: OrderItem[];
  status: string;
  total: number;
}