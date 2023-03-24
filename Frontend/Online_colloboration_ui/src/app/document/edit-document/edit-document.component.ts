import { WebSocketSubject } from 'rxjs/webSocket';
import { DocumentService } from './../../document.service';

import { Component, OnDestroy, OnInit } from '@angular/core';
import Quill, { QuillOptionsStatic } from 'quill';
// import 'quill/dist/quill.snow.css'
import { ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import Delta from 'quill-delta';
import { StompService } from 'src/app/services/stomp.service';

@Component({
  selector: 'app-edit-document',
  templateUrl: './edit-document.component.html',
  styleUrls: ['./edit-document.component.css']
})
export class EditDocumentComponent implements OnInit , OnDestroy {
  private readonly destroy$ = new Subject<void>();
  private readonly documentId: number;
  private socket!: WebSocketSubject<any>;
  public editor!: Quill;
  public documentContent!: string;

  constructor(private route: ActivatedRoute,private stompService: StompService) {
    this.documentId = this.route.snapshot.params['id'];
  }



  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit(): void {
    // Subscribe to updates from the server
    this.stompService.subscribe('/topic/document/'+this.documentId, (deltaString: string) => {
      const delta = JSON.parse(deltaString);
      this.editor.updateContents(delta, 'api');
    });

    var quillOptions: QuillOptionsStatic = {
      debug: 'info',
      placeholder: 'Compose an epic...',
      readOnly: false,
      theme: 'snow',
      modules: {
        toolbar: [
          ['bold', 'italic', 'underline', 'strike'],
          [{ 'header': 1 }, { 'header': 2 }],
          [{ 'list': 'ordered' }, { 'list': 'bullet' }],
          [{ 'indent': '-1' }, { 'indent': '+1' }],
          [{ 'align': [] }],
          ['link', 'image', 'video'],
          ['clean']
        ]
      }
  };
  this.editor=new Quill('#editor', quillOptions)

    // Initialize the Quill editor

    // Subscribe to editor changes and send them to the server
    this.editor.on('text-change', (delta, oldDelta, source) => {
      if (source === 'user') {
        const content =this.editor.getText();
        const deltaString = JSON.stringify(delta);
        const message = { 'delta': deltaString, 'content': content };
        this.stompService.sendMessage(`/app/document/${this.documentId}`, JSON.stringify(message));
      }
    });
  }

  }
