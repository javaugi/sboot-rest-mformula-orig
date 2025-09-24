export class User {
  id: number;
  name: string;
  email: string;
  isActive?: boolean; // Optional property    
}

/*
export interface User {
  id: number;
  name: string;
  email: string;
  isActive?: boolean; // Optional property
}

// Using the interface to enforce type safety
user: User = {
  id: 1,
  name: 'Jane Doe',
  email: 'jane.doe@email.com'
  // isActive is optional, so we can omit it
};
*/