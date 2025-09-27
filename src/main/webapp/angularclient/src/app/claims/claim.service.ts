import { Injectable } from '@angular/core';
import { Claim, PrepayEligiblePair } from './claim.model';

@Injectable({
  providedIn: 'root'
})
export class ClaimService {
  private claims: Claim[] = [
    // Sample data
    {
      id: 'P001', patientId: 'PAT123', encounterId: 'ENC001', 
      claimType: 'Professional', serviceDate: new Date('2024-01-15'),
      submittedDate: new Date('2024-01-16'), provider: 'Dr. Smith', 
      amount: 150, status: 'Paid'
    },
    {
      id: 'F001', patientId: 'PAT123', encounterId: 'ENC001', 
      claimType: 'Facility', serviceDate: new Date('2024-01-15'),
      submittedDate: new Date('2024-01-20'), provider: 'City Hospital', 
      amount: 1200, status: 'Pending'
    },
    {
      id: 'P002', patientId: 'PAT456', encounterId: 'ENC002', 
      claimType: 'Professional', serviceDate: new Date('2024-02-01'),
      submittedDate: new Date('2024-02-02'), provider: 'Dr. Johnson', 
      amount: 200, status: 'Paid'
    }
  ];

  findPrepayEligiblePairs(): PrepayEligiblePair[] {
    const pairs: PrepayEligiblePair[] = [];
    
    // Group claims by patient and encounter
    const groupedClaims = this.claims.reduce((acc, claim) => {
      const key = `${claim.patientId}-${claim.encounterId}`;
      if (!acc[key]) acc[key] = [];
      acc[key].push(claim);
      return acc;
    }, {} as { [key: string]: Claim[] });

    // Find eligible pairs
    Object.values(groupedClaims).forEach(claims => {
      if (claims.length < 2) return;

      const professionalClaim = claims.find(c => c.claimType === 'Professional');
      const facilityClaim = claims.find(c => c.claimType === 'Facility');

      if (professionalClaim && facilityClaim) {
        const daysBetween = Math.abs(
          (professionalClaim.serviceDate.getTime() - facilityClaim.serviceDate.getTime()) / 
          (1000 * 60 * 60 * 24)
        );

        if (daysBetween <= 30) {
          pairs.push({
            professionalClaim,
            facilityClaim,
            daysBetween: Math.round(daysBetween),
            patientId: professionalClaim.patientId,
            encounterId: professionalClaim.encounterId
          });
        }
      }
    });

    return pairs;
  }

  addClaim(claim: Claim) {
    this.claims.push(claim);
  }

  getClaims() {
    return this.claims;
  }
}
