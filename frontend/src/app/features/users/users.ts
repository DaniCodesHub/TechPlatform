import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../models/user';
import { Navbar } from '../../shared/navbar/navbar';

@Component({
  selector: 'app-users',
  imports: [Navbar],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class Users implements OnInit {

  private readonly userService = inject(UserService);
  private readonly cdr = inject(ChangeDetectorRef);

  users: User[] = [];

  ngOnInit(): void {
    this.userService.getUsers().subscribe({
      next: users => {
        this.users = users;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al cargar usuarios:', error);
      }
    });
  }
}