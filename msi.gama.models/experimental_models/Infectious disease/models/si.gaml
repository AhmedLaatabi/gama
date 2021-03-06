/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model si

/* Here is the model definition here */

global { 
 //       var numberHosts type: int init: 100 parameter: 'Number of Hosts';
        int number_S <- 990 parameter: 'Number of Susceptible';  // The number of susceptible
        int number_I <- 10 parameter: 'Number of Infected';	// The number of infected
		float survivalProbability <- 1/(70*365) parameter: 'Survival Probability'; // The survival probability
		float beta <- 0.05 parameter: 'Beta (S->I)'; 	// The parameter Beta
		float nu <- 0.001 parameter: 'Mortality';	// The parameter Nu
//		int timestep <- 100 parameter: "Time step";	// The parameter Beta
		int numberHosts <- number_S+number_I;
		float R0 ;

        init {
                create Host number: number_S {
                	set is_susceptible <- true;
                	set is_infected value: false;
                	set is_immune value: false; 
                	set color value: rgb('green');
                }
                create Host number: number_I {
                	set is_susceptible value: false;
                	set is_infected value: true;
                	set is_immune value: false; 
                	set color value: rgb('red');
                }
        }
        
 //   	reflex shouldHalt when: (time > timestep) or (empty (Host as list)) {
 //       		do action: halt;
 //   	}
        
}

environment width: 100 height: 100 {
        grid si_grid width: 100 height: 100 {
                var color type: rgb init: rgb('black');
                list neighbours of: si_grid <- self neighbours_at 4;
                
           }
  }

entities {
        species Host skills: [moving] {
				bool is_susceptible <- true;
                bool is_infected <- false;
                bool is_immune <- false;
                rgb color value: rgb('green');
                int sic_count <- 0;
                var myPlace type: si_grid function: {location as si_grid};
                
                reflex basic_move {
                        let destination var: destination value: one_of (myPlace.neighbours) ; //where empty(each.agents);
                      	set location value: destination;
                        }        
                reflex become_infected when: (is_susceptible and flip(beta*(length(list(Host) where each.is_infected)/numberHosts))) {
                        set is_susceptible value: false;
                        set is_infected value: true;
                        set is_immune value: false;
                        set color value: rgb('red');
                }
        		reflex shallDie when: flip(nu) {
						create species: species(self) number: 1 {
							set myPlace var: myPlace value: myself.myPlace ;
							set location var: location value: myself.location ;
						}
            			do action: die;
        		}
                aspect basic {
//                        draw shape: circle color: color size: 1;
						draw shape: circle;
                }
        }

}

experiment default_expr type: gui {
	output {
	    display seir_display {
	        grid seir_grid;
	        species Host aspect: basic;
	    }
	        
	    display chart refresh_every: 1 {
			chart name: 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				data susceptible value: (Host as list) count (each.is_susceptible) color: rgb('green');
				data infected value: (Host as list) count (each.is_infected) color: rgb('red');
			}
		}
			
	}
}
