import { Doc } from '../models/Doc';
import { Router } from '@angular/router';
import { DocService } from './../services/doc.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {


  ngOnInit(): void {
  }

  constructor(private docService: DocService, private router: Router) { }

  createDoc() {
    const newDoc: Doc = {
      title: '',
      content: '',
      owner: undefined,
      sharedWith: []
    };
    this.docService.createDoc(newDoc).subscribe(doc => {
      this.router.navigate(['/doc', doc.id]);
    });
  }

}
