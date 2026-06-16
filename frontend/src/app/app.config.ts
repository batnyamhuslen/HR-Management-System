import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { OverlayModule, Overlay } from '@angular/cdk/overlay';
import { MAT_DIALOG_DEFAULT_OPTIONS, MatDialogConfig } from '@angular/material/dialog';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS, MatSnackBarConfig } from '@angular/material/snack-bar';

import { routes } from './app.routes';

import { jwtInterceptor } from './core/interceptors/jwt-interceptor';

const dialogConfig: MatDialogConfig = {
  hasBackdrop: true,
  disableClose: false,
  panelClass: 'custom-dialog-container',
  position: { top: '50px' }
};

const snackBarConfig: MatSnackBarConfig = {
  duration: 3000,
  horizontalPosition: 'center',
  verticalPosition: 'bottom',
  panelClass: 'custom-snackbar-container'
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideAnimationsAsync(),
    OverlayModule,
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: dialogConfig },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: snackBarConfig }
  ]
};