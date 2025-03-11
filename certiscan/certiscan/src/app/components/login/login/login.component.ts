import { Component } from '@angular/core';
import { NavbarComponent } from "../../navbar/navbar.component";
import { AuthService } from '../../../services/auth.service';
import { FormGroup,Validators,FormBuilder } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
username: any;
password: any;
message="";

  loginForm:FormGroup;
  isLoading=false;
  error='';
  

  constructor(private fb: FormBuilder,private authService: AuthService,private router:Router){
    this.loginForm=this.fb.group({
      userName: ['',Validators.required],
      Password: ['',Validators.required]
        })
  }
  get userName(){return this.loginForm.get('userName'); }
  get Password(){ return this.loginForm.get('Password');}
  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.error = '';

      this.authService.signIn(
        this.loginForm.get('userName')?.value,
        this.loginForm.get('Password')?.value
      ).pipe(
        finalize(() => this.isLoading = false)
      ).subscribe({
        next: () => {
          this.router.navigate(['/generate']);
        },
        error: (error) => {
          this.error = error?.error?.message || "Nom d'utilisateur ou mot de passe invalide ";
          this.loginForm.get('Password')?.reset();
        }
      });
    }
  }
}
