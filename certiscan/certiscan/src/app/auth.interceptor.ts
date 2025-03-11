import { HttpRequest, HttpHandlerFn, HttpEvent, HttpInterceptorFn } from '@angular/common/http';
import { Observable } from 'rxjs';
import { inject } from '@angular/core';
import { AuthService } from './services/auth.service'; // Import AuthService

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const authService = inject(AuthService); // Inject AuthService
  const token = authService.getToken(); // Get the token

  if (token) {
    const reqWithHeader = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`),
    });
    console.log('Headers after adding token:', reqWithHeader.headers.keys().map(key => `${key}: ${reqWithHeader.headers.get(key)}`));
    return next(reqWithHeader);
  }

  return next(req);
};