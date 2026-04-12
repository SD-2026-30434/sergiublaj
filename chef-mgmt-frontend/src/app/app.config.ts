import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection, isDevMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { MessageService } from 'primeng/api';
import { provideStore } from '@ngxs/store';
import { withNgxsReduxDevtoolsPlugin } from '@ngxs/devtools-plugin';
import { withNgxsStoragePlugin } from '@ngxs/storage-plugin';
import { routes } from './app.routes';
import { AuthState } from './feature/auth/store/auth.state';
import { ChefState } from './feature/chefs/store/chef.state';
import { OrderState } from './feature/orders/store/order.state';
import { httpRequestInterceptor } from './core/interceptors/http-request.interceptor';
import { httpResponseInterceptor } from './core/interceptors/http-response.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpRequestInterceptor, httpResponseInterceptor])),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: Aura
      },
    }),
    MessageService,
    provideStore(
      [AuthState, ChefState, OrderState],
      withNgxsReduxDevtoolsPlugin({ disabled: !isDevMode() }),
      withNgxsStoragePlugin({ keys: ['auth'] })
    )
  ]
};
