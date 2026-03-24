import { Dog }        from './Dog';
import { Cat }        from './Cat';
import { Shelter }    from './Shelter';
import { Report }     from './Report';
import { MathUtils }  from './MathUtils';

/** Entry point: wires together all classes. */
function main(): void {
    const shelter = new Shelter('Happy Paws', 20);

    shelter.intake(new Dog('Buddy',    3, 'Labrador',        28.5));
    shelter.intake(new Dog('Max',      7, 'German Shepherd', 33.0));
    shelter.intake(new Dog('Tiny',     1, 'Chihuahua',        2.1));
    shelter.intake(new Cat('Whiskers', 5, true,   4.2));
    shelter.intake(new Cat('Shadow',  10, false,  5.8));
    shelter.intake(new Cat('Luna',     2, true,   3.9));

    console.log(new Report(shelter).generate());

    console.log(`\nfibonacci(10) = ${MathUtils.fibonacci(10)}`);
    console.log(`factorial(6)  = ${MathUtils.factorial(6)}`);

    console.log('\nAdopting \'Max\'...');
    const adopted = shelter.adopt('Max');
    console.log(adopted ? `  Adopted: ${adopted.name}` : '  Not found.');

    console.log('\nPost-adoption report:');
    console.log(new Report(shelter).generate());
}

main();
