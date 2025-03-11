import { Routes } from '@angular/router';
import {GenerateCertificateComponent} from './components/certificate/generate/generate/generate.component';
import { DownloadComponent } from './components/certificate/download/download/download.component';
import { LoginComponent } from './components/login/login/login.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = []=[
    
    { 
        path: 'generate', 
        component: GenerateCertificateComponent,
        canActivate: [AuthGuard]
      },
      {path: 'download/:certificateNumber',component: DownloadComponent},
     {path: 'download',component: DownloadComponent},
     {path: 'login',component: LoginComponent},
     
     { path: '', redirectTo: '/download', pathMatch: 'full' as const }
]
