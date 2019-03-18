import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { PatternService } from '../services/pattern.service';
import { NgForm } from '@angular/forms';

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

  save(form: NgForm) {
    if (form.name.trim() != "") {
      this.patternService.add(form).subscribe(result => {
        this.gotoList();
      }, error => console.error(error));
    } else {
      alert("Pattern name cannot be blank");
    }
  }

  remove(href) {
    this.patternService.remove(href).subscribe(result => {
      this.gotoList();
    }, error => console.error(error));
  }

  cancel() {
    this.gotoList();
  }

}
