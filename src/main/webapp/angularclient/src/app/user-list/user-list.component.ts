import { Component } from '@angular/core';
import { User } from '../model/user';
import { UserService } from '../service/user.service';


@Component({
  selector: 'app-user-list',
  imports: [],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {

  //users: User[];
  //names: string[];
  
  users: any[] = [];
  names: any[] = [];

  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.userService.findAll()
        .subscribe(data => {this.users = data;});
    

    this.userService.getUsers()
        .pipe(
            map(users => users.map(u => u.name.toUpperCase()))
        )
        .subscribe(names => this.names = names);    
    }

}
