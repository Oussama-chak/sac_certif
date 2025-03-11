import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  isMenuOpen = false;
  constructor(private router: Router) {}

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu(): void {
    this.isMenuOpen = false;
  }
  navigatetogenerate(): void {
    this.router.navigate(['/generate'])
    this.isMenuOpen = false;
  }
  navigatetodownload(): void{
    this.router.navigate(['/download'])
    this.isMenuOpen = false;
  }
}
