import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PatternComponent } from './pattern/pattern.component';
import { PatternEditComponent } from './pattern-edit/pattern-edit.component';

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {
    path:'home',
    component: PatternComponent
  },
  {
    path: 'pattern-list',
    component: PatternComponent
  },
  {
    path: 'pattern-edit',
    component: PatternEditComponent
  },
  {
    path: 'pattern-edit/:id',
    component: PatternEditComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
