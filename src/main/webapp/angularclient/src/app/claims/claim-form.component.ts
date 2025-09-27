import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Claim } from '../claim.model';
import { ClaimService } from '../claim.service';

@Component({
  selector: 'app-claim-form',
  templateUrl: './claim-form.component.html'
})
export class ClaimFormComponent {
  claimForm: FormGroup;

  constructor(private fb: FormBuilder, private claimService: ClaimService) {
    this.claimForm = this.fb.group({
      patientId: ['', Validators.required],
      encounterId: ['', Validators.required],
      claimType: ['', Validators.required],
      serviceDate: ['', Validators.required],
      submittedDate: ['', Validators.required],
      provider: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0)]]
    });
  }

  onSubmit() {
    if (this.claimForm.valid) {
      const newClaim: Claim = {
        id: this.generateId(),
        ...this.claimForm.value,
        serviceDate: new Date(this.claimForm.value.serviceDate),
        submittedDate: new Date(this.claimForm.value.submittedDate),
        status: 'Pending'
      };

      this.claimService.addClaim(newClaim);
      this.claimForm.reset();
      alert('Claim added successfully!');
    }
  }

  private generateId(): string {
    return Math.random().toString(36).substr(2, 9).toUpperCase();
  }
}
