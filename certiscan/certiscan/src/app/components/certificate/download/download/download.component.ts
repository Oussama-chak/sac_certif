import { Component, OnDestroy } from '@angular/core';
import { NavbarComponent } from "../../../navbar/navbar.component";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CertificateService } from '../../../../services/certificate.service';
import { CommonModule } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-download',
  standalone: true,
  imports: [NavbarComponent, ReactiveFormsModule, CommonModule],
  templateUrl: './download.component.html',
  styleUrl: './download.component.scss'
})
export class DownloadComponent implements OnDestroy {
  private destroy$ = new Subject<void>();
  verifForm: FormGroup;
  submitted = false;
  generationMessage = "";
  errorMessage: string = "";
  
  constructor(
    private fb: FormBuilder, 
    private certifService: CertificateService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.verifForm = this.fb.group({
      certificateNumber: ['', [Validators.required, Validators.pattern('^[0-9]+$')]]
    });
    
    // Handle URL parameter once during construction
    const certNumber = this.route.snapshot.paramMap.get('certificateNumber');
    if (certNumber) {
      const parsedNumber = parseInt(certNumber, 10);
      if (!isNaN(parsedNumber)) {
        this.downloadCertificate(parsedNumber);
      }
    }
  }

  get certificateNumber() { 
    return this.verifForm.get('certificateNumber'); 
  }

  onSubmit(): void {
    this.submitted = true;
  
    if (this.verifForm.invalid) {
      this.verifForm.markAllAsTouched();
      return;
    }
  
    const certificatenumber = Number(this.verifForm.value.certificateNumber.trim());
  
    if (isNaN(certificatenumber)) {
      this.generationMessage = "Numéro de certificat invalide";
      return;
    }
  
    this.certifService.verifierCertificat({ certificatenumber }).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (response: HttpResponse<Blob>) => {
        const blob = response.body;
        if (blob) {
          const contentDisposition = response.headers.get('Content-Disposition');
          let fileName = 'certificate.pdf';
          if (contentDisposition) {
            const filenameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
            if (filenameMatch && filenameMatch[1]) {
              fileName = filenameMatch[1];
            }
          }
          const pdfBlob = new Blob([blob], { type: 'application/pdf' });
          this.downloadFile(pdfBlob, fileName);
        } else {
          this.generationMessage = 'Certificat introuvable pour ce numéro';
        }
      },
      error: () => {
        this.generationMessage = 'Pas de certificat pour cet utilisateur';
      }
    });
  
    this.verifForm.reset();
  }

  private downloadFile(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob);
    const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);
  
    if (isMobile) {
      const newWindow = window.open(url, '_blank');
      if (!newWindow) {
        const iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        iframe.src = url;
        document.body.appendChild(iframe);
        setTimeout(() => {
          document.body.removeChild(iframe);
          window.URL.revokeObjectURL(url);
        }, 1000);
      } else {
        setTimeout(() => {
          window.URL.revokeObjectURL(url);
        }, 1000);
      }
    } else {
      const link = document.createElement('a');
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    }
  }

  downloadCertificate(certificateNumber: number) {
    this.certifService.downloadCertificate(certificateNumber).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (response: HttpResponse<Blob>) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        const filename = contentDisposition 
          ? contentDisposition.split('filename=')[1]
          : `certificate_${certificateNumber}.pdf`;
  
        const blob = response.body;
        if (blob) {
          const pdfBlob = new Blob([blob], { type: 'application/pdf' });
          this.downloadFile(pdfBlob, filename);
          
          if (!/iPhone|iPad|iPod|Android/i.test(navigator.userAgent)) {
            this.router.navigate(['/']);
          }
        }
      },
      error: (error: any) => {
        console.error('Download error:', error);
        this.errorMessage = 'Erreur lors du téléchargement du certificat';
      }
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}