import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';

interface AuthResponse {
  token: string;
  userId: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  

  private signinurl = "http://147.93.59.184/api/v1/auth/signin";
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_DATA_KEY = 'user_data';
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.checkTokenExpiration();
  }

  signIn(username: string, password: string): Observable<AuthResponse> {
    const body = { username, password };
    return this.http.post<AuthResponse>(this.signinurl, body).pipe(
      tap(response => {
        if (response.token) {
          this.setAuthData(response);
          this.isAuthenticatedSubject.next(true);
        }
      })
    );
  }

   getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

signOut(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_DATA_KEY);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
}

  
  

  private setAuthData(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem(this.USER_DATA_KEY, JSON.stringify({
      userId: response.userId,
      role: response.role
    }));
  }

  getUserData(): { userId: string; role: string } | null {
    const data = localStorage.getItem(this.USER_DATA_KEY);
    return data ? JSON.parse(data) : null;
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  private checkTokenExpiration(): void {
    // Check token expiration every minute
    setInterval(() => {
      const token = this.getToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          if (payload.exp * 1000 < Date.now()) {
            this.signOut();
          }
        } catch (e) {
          this.signOut();
        }
      }
    }, 60000);
  }

  // Helper method to get auth headers
  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : new HttpHeaders();
  }

  
}