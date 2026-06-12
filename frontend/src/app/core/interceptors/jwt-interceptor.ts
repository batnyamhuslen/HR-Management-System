import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        authService.logout();
      }
      return throwError(() => err);
    })
  );
};

// import { HttpInterceptorFn, HttpErrorResponse, HttpRequest, HttpHandlerFn } from '@angular/common/http';
// import { inject, PLATFORM_ID } from '@angular/core';
// import { isPlatformBrowser } from '@angular/common';
// import { catchError, throwError } from 'rxjs';
// import { AuthService } from '../services/auth.service';

// export const jwtInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
//   const platformId = inject(PLATFORM_ID);
//   const authService = inject(AuthService);

//   if (!isPlatformBrowser(platformId)) {
//     return next(req);
//   }

//   const token = authService.getToken();

//   if (token) {
//     req = req.clone({
//       setHeaders: { Authorization: `Bearer ${token}` }
//     });
//   }

//   return next(req).pipe(
//     catchError((err: HttpErrorResponse) => {
//       if (err.status === 401) {
//         authService.logout();
//       }
//       return throwError(() => err);
//     })
//   );
// };