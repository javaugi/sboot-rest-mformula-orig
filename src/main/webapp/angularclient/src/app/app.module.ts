import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
//import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { UserListComponent } from './user-list/user-list.component';
import { UserFormComponent } from './user-form/user-form.component';
import { UserService } from './service/user.service';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
//import { AppComponent } from './app.component';
import { AuthInterceptor } from './auth/auth.interceptor'; // Import the interceptor

import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

import { ClaimFormComponent } from './claims/claim-form.component';
import { PrepayPairsComponent } from './claims/prepay-pairs.component';

@NgModule({
  declarations: [
    AppComponent,
    UserListComponent,
    UserFormComponent,
    ClaimFormComponent,
    PrepayPairsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      useClass: UserService,
    }
  ],  
  bootstrap: [AppComponent]
})
export class AppModule { }

/*
This minimal Angular UI provides:

Features:
    Form to input claim data
    Automatic detection of prepay-eligible pairs (Professional + Facility claims for same patient/encounter within 30 days)
    Real-time pair identification
    Clean Material Design interface
    Sample data to demonstrate functionality

Key Logic:
    Groups claims by patient and encounter
    Checks for both Professional and Facility claim types
    Validates service dates are within 30 days
    Displays pairs with relevant information

To run: ng serve and navigate to http://localhost:4200

*/