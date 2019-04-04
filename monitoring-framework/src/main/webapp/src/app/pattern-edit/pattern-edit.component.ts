import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { PatternService } from '../services/pattern.service';
import { NgForm } from '@angular/forms';
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-pattern-edit',
  templateUrl: './pattern-edit.component.html',
  styleUrls: ['./pattern-edit.component.css']
})
export class PatternEditComponent implements OnInit, OnDestroy {
  pattern: any = {};

  sub: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patternService: PatternService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.patternService.get(id).subscribe((pattern: any) => {
          if (pattern) {
            this.pattern = pattern;
          } else {
            console.log(`Pattern with id '${id}' not found, returning to list`);
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
    this.router.navigate(['/pattern-list']);
  }

  openSnackBar(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, {
      duration: duration,
    });
  }

  save(form: NgForm) {
    if (form.name.trim() != "") {
      this.patternService.add(form).subscribe(response => {
        this.gotoList();
      }, error => {
        console.error(error);
      });
    } else {
      this.openSnackBar('Pattern name must not be empty', 'close', 2000)
    }
  }

  remove(form: NgForm) {
    this.patternService.remove(form).subscribe(result => {
      this.gotoList();
    }, error => console.error(error));
  }

  cancel() {
    this.gotoList();
  }

}
