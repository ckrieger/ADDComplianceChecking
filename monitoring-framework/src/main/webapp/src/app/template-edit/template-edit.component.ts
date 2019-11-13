import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { PatternService } from '../services/pattern.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NgForm } from '@angular/forms';
import { TemplateService } from '../services/template.service';

@Component({
  selector: 'app-template-edit',
  templateUrl: './template-edit.component.html',
  styleUrls: ['./template-edit.component.css']
})
export class TemplateEditComponent implements OnInit {

  template: any = {};

  sub: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private templateService: TemplateService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.templateService.get(id).subscribe((template: any) => {
          if (template) {
            this.template = template;
          } else {
            console.log(`Template with id '${id}' not found, returning to list`);
            //this.gotoList();
          }
        });
      }
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  gotoList() {
    this.router.navigate(['/template-list']);
  }

  openSnackBar(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, {
      duration: duration,
    });
  }

  save(form: NgForm) {
    if (form.name.trim() != "") {
      let template = {
        name : form.name,
        content: form.content,
        patterns: []
      }
      this.templateService.add(template).subscribe(response => {
        this.gotoList();
      }, error => {
        console.error(error);
      });
    } else {
      this.openSnackBar('Template name must not be empty', 'close', 2000)
    }
  }

  remove(form: NgForm) {
    this.templateService.remove(form).subscribe(result => {
      this.gotoList();
    }, error => console.error(error));
  }

  cancel() {
    this.gotoList();
  }

}
