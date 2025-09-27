import { Component } from '@angular/core';
import { ClaimService } from '../claim.service';
import { PrepayEligiblePair } from '../claim.model';

@Component({
  selector: 'app-prepay-pairs',
  templateUrl: './prepay-pairs.component.html'
})
export class PrepayPairsComponent {
  eligiblePairs: PrepayEligiblePair[] = [];

  constructor(private claimService: ClaimService) {
    this.findEligiblePairs();
  }

  findEligiblePairs() {
    this.eligiblePairs = this.claimService.findPrepayEligiblePairs();
  }

  refresh() {
    this.findEligiblePairs();
  }
}
