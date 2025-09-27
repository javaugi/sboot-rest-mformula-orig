export interface Claim {
  id: string;
  patientId: string;
  encounterId: string;
  claimType: 'Professional' | 'Facility';
  serviceDate: Date;
  submittedDate: Date;
  provider: string;
  amount: number;
  status: string;
}

export interface PrepayEligiblePair {
  professionalClaim: Claim;
  facilityClaim: Claim;
  daysBetween: number;
  patientId: string;
  encounterId: string;
}
