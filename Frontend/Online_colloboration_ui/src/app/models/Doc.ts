import { User } from "./User";

export interface Doc {
  id?: number;
  title: string;
  content: string;
  owner?: User;
  sharedWith?: User[];
}
