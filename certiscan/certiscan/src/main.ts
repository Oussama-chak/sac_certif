// src/main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { RouterModule } from '@angular/router';  // Import RouterModule
import { routes } from './app/app.routes'; 
import { importProvidersFrom } from '@angular/core';
bootstrapApplication(AppComponent, {
  providers: [provideHttpClient(withFetch()),importProvidersFrom(RouterModule.forRoot(routes))]
})