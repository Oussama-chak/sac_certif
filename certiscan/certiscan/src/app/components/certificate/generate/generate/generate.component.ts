import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpResponse } from '@angular/common/http';
import { CertificateService } from '../../../../services/certificate.service';
import { NavbarComponent } from "../../../navbar/navbar.component";

@Component({
  selector: 'app-generate-certificate',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, NavbarComponent],
  templateUrl: './generate.component.html',
  styleUrl: './generate.component.scss'
})
export  class GenerateCertificateComponent {
  certificateData = {
    title: '',
    
    firstName: '',
    lastName: '',
    cin: null,
    date: '',
    instructor: ''
  };
  selectedFile: File|null=null;
  generationMessage: string = '';
  certificateNumber: string | undefined;
  templatemessage: string=""

  constructor(private certificateService: CertificateService) {}

  onSubmit() {
    this.certificateService.generateCertificate(this.certificateData)
      .subscribe({
        next: (response: HttpResponse<Blob>) => {
          const pdfBlob = response.body;
          
          if (pdfBlob && pdfBlob.size > 0) {
            // Extract filename from Content-Disposition header
            const contentDisposition = response.headers.get('Content-Disposition');
            let fileName = 'certificate.pdf'; // Default filename
  
            if (contentDisposition) {
              const filenameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
              if (filenameMatch && filenameMatch[1]) {
                fileName = filenameMatch[1];
              }
            }
  
            // Create object URL
            const fileURL = URL.createObjectURL(pdfBlob);
  
            // Open in new tab
            window.open(fileURL, '_blank');
  
            // Trigger download
            const downloadLink = document.createElement('a');
            downloadLink.href = fileURL;
            downloadLink.download = fileName;
            document.body.appendChild(downloadLink);
            downloadLink.click();
            document.body.removeChild(downloadLink);
            this.generationMessage="Certificat générer avec succès"
            setTimeout(() => {
              this.generationMessage = "";
            }, 2000);
            // Free memory
            URL.revokeObjectURL(fileURL);
          } else {
            console.error('Empty PDF blob');
          }
        },
        error: (error) => {
          console.error('Error generating certificate:', error);
          if (error.status === 409) {  // Assuming the backend sends a 409 Conflict status
            this.generationMessage = error.error.message || "Erreur : Certificat déjà existant pour cette personne !";
          } else {
            this.generationMessage = "Erreur lors de la génération du certificat !";
          }
          setTimeout(() => {
            this.generationMessage = "";
          }, 2000);
        }
        
      });
      this. certificateData = {
        title: '',
        
        firstName: '',
        lastName: '',
        cin: null,
        date: '',
        instructor: ''
      };
  }
  
    // Date formatting utility
    private formatDate(dateString: string): string {
      return new Date(dateString).toISOString().split('T')[0];
    }



    onFileSelected(event: any) {
      const file = event.target.files[0];
      const fileNameDisplay = document.getElementById("fileName");
    
      if (file) {
        this.selectedFile = file;
        fileNameDisplay!.textContent = file.name; // Update the file name in the UI
      } else {
        this.selectedFile = null;
        fileNameDisplay!.textContent = "Aucun fichier sélectionné";
      }
    }

    uploadtemplate() {
      if (!this.selectedFile) {
        this.templatemessage="pas de fichier selectioné !"
        setTimeout(() => {
          this.templatemessage = "";
        }, 2000);
        return;
      }
    
      this.certificateService.ajoutertemplate(this.selectedFile).subscribe({
        next: (response) => {
          this.templatemessage="Template ajouté avec succès."
          setTimeout(() => {
            this.templatemessage = "";
          }, 2000);
        },
        error: (error) => {
          this.templatemessage="Erreur d'ajout de template !"
          setTimeout(() => {
            this.templatemessage = "";
          }, 2000);
          
        }
      });
    }
  
}