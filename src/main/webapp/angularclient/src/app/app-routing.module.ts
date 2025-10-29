// src/app/app-routing.module.ts
import { NgModule } from '@angular/core';
import { Routes } from '@angular/router';
//import { Routes, RouterModule } from '@angular/router';
import { RouterModule } from '@angular/router';
import { UserListComponent } from './user-list/user-list.component';
import { UserFormComponent } from './user-form/user-form.component';
import { HelloWorldComponent } from './hello-world/hello-world.component';
import { AuthGuard } from './auth/auth.guard'; // Import your guard 

export const routes: Routes = [
  { path: 'users', component: UserListComponent },
    { path: 'adduser', component: UserFormComponent },
    { path: '', redirectTo: '/helloDashboard', pathMatch: 'full' },
    {
        path: 'helloDashboard', component: HelloWorldComponent, // This is where you apply the guard: 
        canActivate: [AuthGuard]
    }, // Assuming a HomeComponent
    {
        path: 'admin',
        loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
    }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { } 
