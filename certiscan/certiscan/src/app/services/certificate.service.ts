import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { from, Observable, switchMap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  private apiUrl = 'http://147.93.59.184:8008/api/certificates';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem("auth_token"); // Assuming the token is stored in localStorage
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }
  

  generateCertificate(certificateData: any): Observable<HttpResponse<Blob>> {
    return this.http.post(`${this.apiUrl}/generate`, certificateData, {
      headers: this.getHeaders(), // Attach Authorization headers
      observe: 'response',
      responseType: 'blob',
    });
  }
  verifierCertificat(payload: { certificatenumber: number }): Observable<HttpResponse<Blob>> {
    return this.http.post(`${this.apiUrl}/download`, payload, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  ajoutertemplate(file: File): Observable<any> {
    return from(this.readFileAsBase64(file)).pipe(
      switchMap(base64String => {
        const payload = {
          pdfdata: base64String
        };
        console.log('Sending request payload:', payload);
        return this.http.post(`${this.apiUrl}/template`, payload);
      })
    );
  }
  
  private readFileAsBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      
      reader.onloadend = () => {
        const base64String = (reader.result as string).split(',')[1];
        resolve(base64String);
      };
      
      reader.onerror = (error) => {
        reject(error);
      };
  
      reader.readAsDataURL(file);
    });
  }

  downloadCertificate(certificateNumber: number): Observable<any> {
    console.log('Service: Downloading certificate number:', certificateNumber);
    const url = `${this.apiUrl}/download/${certificateNumber}`;
    console.log('Request URL:', url);
    
    return this.http.get(url, {
      responseType: 'blob', 
      observe: 'response' 
    });
  }
  
  
  
}
