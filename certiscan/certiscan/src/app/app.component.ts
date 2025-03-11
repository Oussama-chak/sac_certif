import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {GenerateCertificateComponent} from "./components/certificate/generate/generate/generate.component";
import { HttpClientModule , HttpClient} from '@angular/common/http';
import { LoginComponent } from "./components/login/login/login.component";
import { FooterComponent } from "./footer/footer.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule, RouterOutlet, FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'certiscan';
}
