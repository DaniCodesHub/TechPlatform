import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginResponse {
  token: string;
}

@Service()
export class Auth {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/auth';

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, {
      email,
      password
    });
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }
}